package main

import (
	"testing"

	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/test"
)

func TestInstructions(t *testing.T) {
	a := NewApp()
	form := makeApiKeyUI(a)

	if form.instructions.Text != "Enter in the Server's Connection Details" {
		t.Fatal("unexpected instructions text")
	}
}

func TestApiKeyEntryIsBlankUponCreation(t *testing.T) {
	a := NewApp()
	form := makeApiKeyUI(a)

	if len(form.apiKeyEntry.Text) != 0 {
		t.Fatal("api key entry text was not empty")
	}
}

func TestSubmitButtonIsDisabledUntilValidationPasses(t *testing.T) {
	a := NewApp()
	form := makeApiKeyUI(a)

	if !form.submitButton.Disabled() {
		t.Fatal("expected submit button to be disabled")
	}

	test.Type(form.apiKeyEntry, "hello there")

	if form.submitButton.Disabled() {
		t.Fatal("expected submit button to not be disabled")
	}
}

func TestValidation(t *testing.T) {
	a := NewApp()
	form := makeApiKeyUI(a)

	if form.apiKeyEntry.Validate() == nil {
		t.Fatal("expected api key entry validation to fail due to empty text")
	}

	test.Type(form.apiKeyEntry, "hello there")

	if form.apiKeyEntry.Validate() != nil {
		t.Fatal("expected api key entry validation to not fail")
	}
}

func TestApiKeyOnAppAfterEnteringApiKeyAndPressingSubmit(t *testing.T) {
	a := NewApp()
	form := makeApiKeyUI(a)

	apiKey := "hello there"

	test.Type(form.apiKeyEntry, apiKey)

	test.Tap(form.submitButton)

	if a.apiKey != apiKey {
		t.Fatalf("expected api key '%s', got '%s' instead", apiKey, a.apiKey)
	}
}

func TestApiKeyOnAppAfterEnteringApiKeyAndPressingEnter(t *testing.T) {
	a := NewApp()
	form := makeApiKeyUI(a)

	apiKey := "hello there"

	test.Type(form.apiKeyEntry, apiKey)

	form.apiKeyEntry.TypedKey(&fyne.KeyEvent{
		Name: fyne.KeyEnter,
	})

	if a.apiKey != apiKey {
		t.Fatalf("expected api key '%s', got '%s' instead", apiKey, a.apiKey)
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
