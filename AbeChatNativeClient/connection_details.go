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

func showApiKeyForm(a *App) {
	form := makeApiKeyUI(a)

	content := container.New(
		layout.NewVBoxLayout(),
		form.instructions,
		form.error,
		form.urlEntry,
		form.apiKeyEntry,
		container.New(layout.NewHBoxLayout(), form.progress, form.submitButton),
	)
	a.MainWindow.SetContent(content)
	a.MainWindow.Resize(fyne.NewSize(300, 75))

	a.MainWindow.Canvas().FocusNext()
}

type apiKeyForm struct {
	instructions *widget.Label
	error        *widget.Label
	urlEntry     *widget.Entry
	apiKeyEntry  *widget.Entry
	submitButton *widget.Button
	progress     *widget.ProgressBarInfinite
}

func makeApiKeyUI(a *App) apiKeyForm {
	instructions := widget.NewLabel("Enter in the Server's Connection Details")
	errorLabel := widget.NewLabel("")
	errorLabel.Importance = widget.DangerImportance
	errorLabel.Hidden = true

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
		} else {
			errorLabel.Hidden = true

			progress.Show()
			progress.Start()

			slog.Info("Successfully got connection details, testing connection...", "connection url", urlEntry.Text)

			u, _ := url.ParseRequestURI(urlEntry.Text)
			s := NewService(u, apiKeyEntry.Text)

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

	submitButton := widget.NewButton("Continue", submit)
	submitButton.Disable()

	apiKeyEntry.OnChanged = createOnChangeFunc(apiKeyEntry, submitButton, errorLabel)
	urlEntry.OnChanged = createOnChangeFunc(urlEntry, submitButton, errorLabel)

	return apiKeyForm{
		instructions: instructions,
		error:        errorLabel,
		urlEntry:     urlEntry,
		apiKeyEntry:  apiKeyEntry,
		submitButton: submitButton,
		progress:     progress,
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
