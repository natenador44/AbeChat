package main

import (
	"testing"

	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/test"
)

func TestInstructions(t *testing.T) {
	a := NewApp()
	form := makeApiKeyUI(a, nil, newTestService)

	if form.instructions.Text != "Enter in the Server's Connection Details" {
		t.Fatal("unexpected instructions text")
	}
}

func TestApiKeyEntryIsBlankUponCreation(t *testing.T) {
	a := NewApp()
	form := makeApiKeyUI(a, nil, newTestService)

	if len(form.apiKeyEntry.Text) != 0 {
		t.Fatal("api key entry text was not empty")
	}
}

func TestSubmitButtonIsDisabledUntilValidationPasses(t *testing.T) {
	a := NewApp()
	form := makeApiKeyUI(a, nil, newTestService)

	if !form.submitButton.Disabled() {
		t.Fatal("expected submit button to be disabled")
	}

	test.Type(form.apiKeyEntry, "hello there")

	if !form.submitButton.Disabled() {
		t.Fatal("expected submit button to be disabled with just api key filled")
	}

	test.Type(form.urlEntry, "localhost:9009")

	if form.submitButton.Disabled() {
		t.Fatal("expected submit button to not be disabled")
	}
}

func TestValidation(t *testing.T) {
	a := NewApp()
	form := makeApiKeyUI(a, nil, newTestService)

	if form.apiKeyEntry.Validate() == nil {
		t.Fatal("expected validation to fail due to empty text")
	}

	test.Type(form.apiKeyEntry, "hello there")

	if form.apiKeyEntry.Validate() == nil {
		t.Fatal("expected api key entry validation to fail")
	}

	test.Type(form.urlEntry, "url")

	if form.apiKeyEntry.Validate() != nil {
		t.Fatal("expected api key entry validation to succeed")
	}
}

func TestServiceOnAppAfterEnteringApiKeyAndUrlPressingSubmit(t *testing.T) {
	a := NewApp()
	form := makeApiKeyUI(a, nil, newTestService)

	test.Type(form.apiKeyEntry, "hello there")
	test.Type(form.apiKeyEntry, "url")

	test.Tap(form.submitButton)

	if a.Service == nil {
		t.Fatalf("expected service to not be null")
	}
}

func TestServiceOnAppAfterEnteringApiKeyAndUrlPressingEnter(t *testing.T) {
	a := NewApp()
	form := makeApiKeyUI(a, nil, newTestService)

	test.Type(form.apiKeyEntry, "hello there")
	test.Type(form.urlEntry, "url")

	form.apiKeyEntry.TypedKey(&fyne.KeyEvent{
		Name: fyne.KeyEnter,
	})

	if a.Service == nil {
		t.Fatalf("expected service to not be null")
	}
}

/*
 * Figure out how to mock service calls
 * Future tests
 * - modify above to include URL entry
 * - test that error label is populated and shown if validation fails
 * - test that each entry gets focus when that field has an error
 * - test placeholder text for each entry field
 */

func newTestService(_ ConnectionDetails) Service {
	return &testService{}
}

type testService struct {
}

func (t *testService) Ping() error {
	return nil
}
