package main

import (
	"errors"
	"net/http"
	"net/url"
)

const apiPostfix string = "/api"
const apiKeyHeaderKey string = "X-API-KEY"

type Service struct {
	connectionDetails
}

func NewService(serverUrl *url.URL, apiKey string) *Service {
	if serverUrl.Scheme != "http" && serverUrl.Scheme != "https" {
		serverUrl, _ = url.ParseRequestURI("http://" + serverUrl.String())
	}

	return &Service{
		connectionDetails{
			serverUrl.JoinPath(apiPostfix),
			apiKey,
		},
	}
}

type connectionDetails struct {
	apiUrl *url.URL
	apiKey string
}

func (s *Service) Ping() error {
	pingUrl := s.apiUrl.JoinPath("ping").String()
	req, err := http.NewRequest(http.MethodGet, pingUrl, nil)

	if err != nil {
		return err
	}

	req.Header.Add(apiKeyHeaderKey, s.connectionDetails.apiKey)

	res, err := http.DefaultClient.Do(req)

	if err != nil {
		return err
	}

	if res.StatusCode != http.StatusOK {
		return errors.New("failed to ping server")
	}

	return nil
}
