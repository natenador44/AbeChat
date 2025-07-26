package main

import (
	"errors"
	"log/slog"
	"net/url"

	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/container"
	"fyne.io/fyne/v2/layout"
	"fyne.io/fyne/v2/widget"
)

func showApiKeyForm(a *App, connections []ConnectionDetails) {
	form := makeApiKeyUI(a, connections, NewService)

	content := container.New(
		layout.NewVBoxLayout(),
		form.instructions,
		form.connectionOptions,
		form.error,
		container.New(layout.NewFormLayout(), form.nameLabel, form.nameEntry, form.urlLabel, form.urlEntry, form.apiKeyLabel, form.apiKeyEntry),
		form.submitButton,
	)
	a.MainWindow.SetContent(content)
	a.MainWindow.Resize(fyne.NewSize(400, 100))

	a.MainWindow.Canvas().FocusNext()
}

type apiKeyForm struct {
	instructions      *widget.Label
	connectionOptions *widget.Select
	error             *widget.Label
	nameLabel         *widget.Label
	nameEntry         *widget.Entry
	urlLabel          *widget.Label
	urlEntry          *widget.Entry
	apiKeyLabel       *widget.Label
	apiKeyEntry       *widget.Entry
	submitButton      *widget.Button
	progress          *widget.ProgressBarInfinite
}

func makeApiKeyUI(a *App, connections []ConnectionDetails, serviceCreator func(ConnectionDetails) Service) apiKeyForm {
	instructions := widget.NewLabel("Enter in the Server's Connection Details")
	errorLabel := widget.NewLabel("")
	errorLabel.Importance = widget.DangerImportance
	errorLabel.Hidden = true

	nameEntry := widget.NewEntry()

	urlEntry := widget.NewEntry()
	urlEntry.Validator = func(u string) (err error) {
		if len(u) == 0 {
			err = errors.New("Server URL cannot be empty")
		} else {
			_, err = url.ParseRequestURI(u)
		}
		return
	}
	urlEntry.PlaceHolder = "Server URL"

	apiKeyEntry := widget.NewEntry()
	apiKeyEntry.Validator = func(apiKey string) error {
		if (len(apiKey)) == 0 {
			return errors.New("API Key cannot be empty")
		} else {
			return nil
		}
	}
	apiKeyEntry.PlaceHolder = "API Key"

	progress := widget.NewProgressBarInfinite()
	progress.Hide()

	submit := func() {
		if err := urlEntry.Validate(); err != nil {
			errorLabel.Hidden = false
			errorLabel.SetText(err.Error())
			a.MainWindow.Canvas().Focus(urlEntry)
		} else if err := apiKeyEntry.Validate(); err != nil {
			errorLabel.Hidden = false
			errorLabel.SetText(err.Error())
			a.MainWindow.Canvas().Focus(apiKeyEntry)
		} else if len(nameEntry.Text) == 0 {
			// eventually check for duplicates
			errorLabel.Hidden = false
			errorLabel.SetText("Name cannot be empty")
			a.MainWindow.Canvas().Focus(nameEntry)
		} else {
			errorLabel.Hidden = true

			progress.Show()
			progress.Start()

			slog.Info("Successfully got connection details, testing connection...", "connection url", urlEntry.Text)

			u, _ := url.ParseRequestURI(urlEntry.Text)
			s := serviceCreator(ConnectionDetails{nameEntry.Text, u, apiKeyEntry.Text})

			err := s.Ping()

			progress.Stop()
			progress.Hide()

			if err != nil {
				slog.Error("failed to make connection", "err", err)
				errorLabel.Hidden = false
				errorLabel.SetText("Connection Failed")
			} else {
				slog.Info("connection successful, moving on")
				a.Service = s
			}
			// validate connection -- have an endpoint that validates the api key.
			// if the connection is successfully made and the api key is valid, move forward
			// otherwise show an error
		}
	}

	apiKeyEntry.OnSubmitted = func(_ string) { submit() }
	urlEntry.OnSubmitted = func(_ string) { submit() }

	submitButton := widget.NewButton("Connect", submit)
	submitButton.Disable()

	apiKeyEntry.OnChanged = createOnChangeFunc(apiKeyEntry, submitButton, errorLabel)
	urlEntry.OnChanged = createOnChangeFunc(urlEntry, submitButton, errorLabel)

	options := make([]string, len(connections))
	for i, c := range connections {
		options[i] = c.Name
	}

	connectionOptions := widget.NewSelect(options, func(selected string) {
		for _, c := range connections {
			if c.Name == selected {
				nameEntry.SetText(c.Name)
				urlEntry.SetText(c.ApiUrl.String())
				apiKeyEntry.SetText(c.ApiKey)
			}
		}
	})

	slog.Info("connections", "len", len(connections))
	if len(connections) <= 1 {
		connectionOptions.Disable()
	}

	if len(connections) >= 1 {
		connectionOptions.SetSelectedIndex(0)
	}

	return apiKeyForm{
		instructions:      instructions,
		connectionOptions: connectionOptions,
		error:             errorLabel,
		nameLabel:         widget.NewLabel("Server Name:"),
		nameEntry:         nameEntry,
		urlLabel:          widget.NewLabel("Server API URL:"),
		urlEntry:          urlEntry,
		apiKeyLabel:       widget.NewLabel("API Key:"),
		apiKeyEntry:       apiKeyEntry,
		submitButton:      submitButton,
		progress:          progress,
	}
}

func createOnChangeFunc(e *widget.Entry, submitButton *widget.Button, errorLabel *widget.Label) func(string) {
	return func(newKey string) {
		if err := e.Validate(); err != nil {
			submitButton.Disable()
		} else {
			errorLabel.Hidden = true
			submitButton.Enable()
		}
	}
}
