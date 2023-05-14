package joseluisgs.dev.data

import joseluisgs.dev.models.Racket
import java.time.LocalDateTime

fun racketsDemoData(): MutableMap<Long, Racket> = mutableMapOf(
    1L to Racket(
        1L,
        "Babolat",
        "Pure Drive",
        200.0,
        10,
        "https://www.m1tennis.com/14267-medium_default/raqueta-babolat-pure-drive-2021.jpg",
        LocalDateTime.parse("2021-05-05T00:00:00"),
        LocalDateTime.parse("2021-05-05T00:00:00")
    ),
    2L to Racket(
        2L,
        "Babolat",
        "Pure Aero",
        225.0,
        8,
        "https://www.lawebdeltenis.net/wp-content/uploads/2023/01/Babolat-Pure-Aero-98-696x277.jpg",
        LocalDateTime.parse("2021-05-05T00:00:00"),
        LocalDateTime.parse("2021-05-05T00:00:00")
    ),
    3L to Racket(
        3L,
        "Head",
        "Speed",
        250.25,
        15,
        "https://www.mistertennis.com/images/2022-media/head-speed-mp-racchetta-da-tennis-233612_B.jpg",
        LocalDateTime.parse("2021-05-05T00:00:00"),
        LocalDateTime.parse("2021-05-05T00:00:00")
    ),
    4L to Racket(
        4L,
        "Wilson",
        "Pro Staff",
        300.0,
        12,
        "https://www.tennispro.es/media/catalog/product/cache/7/thumbnail/1200x/9df78eab33525d08d6e5fb8d27136e95/w/r/wr043811u_1_11.jpg",
        LocalDateTime.parse("2021-05-05T00:00:00"),
        LocalDateTime.parse("2021-05-05T00:00:00")
    ),
)