package main

import (
	"errors"
	"fmt"
	"log/slog"
	"net/url"
	"os"
	"path"

	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/app"
)

type App struct {
	inner      fyne.App
	MainWindow fyne.Window
	connectionDetails
}

type connectionDetails struct {
	url    *url.URL
	apiKey string
}

func NewApp() *App {
	app := app.New()
	return &App{
		app,
		nil,
		connectionDetails{nil, ""},
	}
}

func (a *App) Start() error {
	window := a.inner.NewWindow("AbeChat")

	a.MainWindow = window

	slog.Info("Starting up abechat")
	appDir, err := appDir()

	if err != nil {
		return err
	}

	apiKey, err := getApiKey(appDir)

	if err != nil {
		return err
	}

	if len(apiKey) == 0 {
		showApiKeyForm(a)
	} else {
		a.apiKey = apiKey
		// a.screen = &LoginScreen {}
	}

	a.MainWindow.ShowAndRun()

	return nil
}

func getApiKey(appDir string) (string, error) {
	apiKeyBytes, err := os.ReadFile(path.Join(appDir, ".api-key"))

	if err != nil {
		if errors.Is(err, os.ErrNotExist) {
			slog.Info("API Key file not found, prompting..")
			return "", nil
		} else {
			return "", err
		}
	} else if len(apiKeyBytes) == 0 {
		slog.Info("API Key file is empty, prompting..")
		return "", nil
	} else {
		slog.Info("API Key found")
		return string(apiKeyBytes), nil
	}
}

func appDir() (string, error) {
	h, err := os.UserHomeDir()

	if err != nil {
		if errors.Is(err, os.ErrNotExist) {
			err = os.MkdirAll(h, os.ModeDir)

			if err != nil {
				return "", fmt.Errorf("failed to create app dir: %s", err)
			}
		} else {
			return "", nil
		}
	}

	return path.Join(h, ".abechat"), nil
}
