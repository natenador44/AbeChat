package main

import (
	"encoding/json"
	"errors"
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
	Service
}

func NewApp() *App {
	app := app.New()
	return &App{
		app,
		nil,
		nil,
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

	connections, err := getConnectionDetails(appDir)

	if err != nil {
		return err
	}

	if len(connections) != 0 {
		showApiKeyForm(a, connections)
	} else {

		// a.screen = &LoginScreen {}
	}

	a.MainWindow.ShowAndRun()

	return nil
}

func getConnectionDetails(appDir string) ([]ConnectionDetails, error) {
	apiKeyBytes, err := os.ReadFile(path.Join(appDir, "connections.json"))

	if err != nil {
		if errors.Is(err, os.ErrNotExist) {
			slog.Info("Connections file not found")
			return nil, nil
		} else {
			return nil, err
		}
	} else if len(apiKeyBytes) == 0 {
		slog.Info("Connection file is empty")
		return nil, nil
	} else {
		slog.Info("Connections found")
		var existingConns []struct {
			Name      string `json:"name"`
			ServerUrl string `json:"serverUrl"`
			ApiKey    string `json:"apiKey"`
		}
		err := json.Unmarshal(apiKeyBytes, &existingConns)

		connections := make([]ConnectionDetails, len(existingConns))

		for i, c := range existingConns {
			u, err := url.ParseRequestURI(c.ServerUrl)
			if err != nil {
				return nil, err
			}

			connections[i] = ConnectionDetails{
				c.Name,
				u,
				c.ApiKey,
			}
		}

		return connections, err
	}
}

func appDir() (string, error) {
	h, err := os.UserHomeDir()

	if err != nil {
		return "", err
	}

	appDir := path.Join(h, ".abechat")

	err = os.MkdirAll(appDir, os.ModePerm)

	if err != nil {
		return "", err
	}

	return appDir, nil
}
