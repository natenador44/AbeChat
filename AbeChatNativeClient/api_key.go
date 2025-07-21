package main

import (
	"errors"
	"log/slog"

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
		form.apiKeyEntry,
		form.submitButton,
	)
	a.MainWindow.SetContent(content)
	a.MainWindow.Resize(fyne.NewSize(300, 75))

	a.MainWindow.Canvas().FocusNext()
}

type apiKeyForm struct {
	instructions *widget.Label
	error        *widget.Label
	apiKeyEntry  *widget.Entry
	submitButton *widget.Button
}

func makeApiKeyUI(a *App) apiKeyForm {
	instructions := widget.NewLabel("An API Key is required to use AbeChat")
	apiKeyEntryError := widget.NewLabel("")
	apiKeyEntryError.Importance = widget.DangerImportance
	apiKeyEntryError.Hidden = true

	apiKeyEntry := widget.NewEntry()
	apiKeyEntry.Validator = func(apiKey string) error {
		if (len(apiKey)) == 0 {
			return errors.New("API Key cannot be empty")
		} else {
			return nil
		}
	}

	apiKeyEntry.PlaceHolder = "API Key"

	submit := func(newKey string) {
		if err := apiKeyEntry.Validate(); err != nil {
			apiKeyEntryError.Hidden = false
			apiKeyEntryError.SetText(err.Error())
		} else {
			apiKeyEntryError.Hidden = true
			a.apiKey = newKey
			slog.Info("Successfully got an api key, moving on")
		}
	}

	apiKeyEntry.OnSubmitted = submit

	submitButton := widget.NewButton("Continue", func() {
		submit(apiKeyEntry.Text)
	})
	submitButton.Disable()

	apiKeyEntry.OnChanged = func(newKey string) {
		if err := apiKeyEntry.Validate(); err != nil {
			submitButton.Disable()
		} else {
			apiKeyEntryError.Hidden = true
			submitButton.Enable()
		}
	}

	return apiKeyForm{
		instructions: instructions,
		error:        apiKeyEntryError,
		apiKeyEntry:  apiKeyEntry,
		submitButton: submitButton,
	}
}
