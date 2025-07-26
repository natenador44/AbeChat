package main

import (
	"errors"
	"net/http"
	"net/url"
)

const apiPostfix string = "/api"
const apiKeyHeaderKey string = "X-API-KEY"

// interface for testing
type Service interface {
	Ping() error
}

type AbeChatService struct {
	ConnectionDetails
}

func NewService(c ConnectionDetails) Service {
	if c.ApiUrl.Scheme != "http" && c.ApiUrl.Scheme != "https" {
		newUrl, _ := url.ParseRequestURI("http://" + c.ApiUrl.String())
		c.ApiUrl = newUrl
	}

	c.ApiUrl = c.ApiUrl.JoinPath(apiPostfix)

	return &AbeChatService{
		c,
	}
}

type ConnectionDetails struct {
	Name   string
	ApiUrl *url.URL
	ApiKey string
}

func (s *AbeChatService) Ping() error {
	pingUrl := s.ApiUrl.JoinPath("ping").String()
	req, err := http.NewRequest(http.MethodGet, pingUrl, nil)

	if err != nil {
		return err
	}

	req.Header.Add(apiKeyHeaderKey, s.ConnectionDetails.ApiKey)

	res, err := http.DefaultClient.Do(req)

	if err != nil {
		return err
	}

	if res.StatusCode != http.StatusOK {
		return errors.New("failed to ping server")
	}

	return nil
}
