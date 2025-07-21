package main

import (
	"log/slog"
)

func main() {
	a := NewApp()

	err := a.Start()

	if err != nil {
		slog.Error("abechat exited with an error", "error", err)
	} else {
		slog.Info("abechat exited normally")
	}
}
