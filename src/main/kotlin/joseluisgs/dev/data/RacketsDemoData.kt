package joseluisgs.dev.data

import joseluisgs.dev.models.Racket
import joseluisgs.dev.models.Racket.Companion.DEFAULT_IMAGE
import java.time.LocalDateTime


fun racketsDemoData(): MutableMap<Long, Racket> = mutableMapOf(
    1L to Racket(
        1L,
        "Babolat",
        "Pure Drive",
        200.0,
        10,
        DEFAULT_IMAGE,
        LocalDateTime.parse("2021-05-05T00:00:00"),
        LocalDateTime.parse("2021-05-05T00:00:00")
    ),
    2L to Racket(
        2L,
        "Babolat",
        "Pure Aero",
        225.0,
        8,
        DEFAULT_IMAGE,
        LocalDateTime.parse("2021-05-05T00:00:00"),
        LocalDateTime.parse("2021-05-05T00:00:00")
    ),
    3L to Racket(
        3L,
        "Head",
        "Speed",
        250.25,
        15,
        DEFAULT_IMAGE,
        LocalDateTime.parse("2021-05-05T00:00:00"),
        LocalDateTime.parse("2021-05-05T00:00:00")
    ),
    4L to Racket(
        4L,
        "Wilson",
        "Pro Staff",
        300.0,
        12,
        DEFAULT_IMAGE,
        LocalDateTime.parse("2021-05-05T00:00:00"),
        LocalDateTime.parse("2021-05-05T00:00:00")
    ),
)