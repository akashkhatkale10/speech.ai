package com.speechai.speechai.utils

import com.google.firebase.ai.type.Schema

val analyseAudioResponseSchema = Schema.obj(
    properties = mapOf(
        "confidence" to Schema.obj(
            properties = mapOf(
                "score" to Schema.integer(),
                "reason_for_score" to Schema.string()
            )
        ),
        "filler_words" to Schema.obj(
            properties = mapOf(
                "has_filler_words" to Schema.boolean(),
                "score" to Schema.integer(),
                "reason_for_score" to Schema.string(),
                "examples" to Schema.array(
                    items = Schema.string()
                )
            )
        ),
        "mumble" to Schema.obj(
            properties = mapOf(
                "did_mumble" to Schema.boolean(),
                "score" to Schema.integer(),
                "reason_for_score" to Schema.string(),
                "examples" to Schema.array(
                    items = Schema.string()
                )
            )
        ),
        "fluency" to Schema.obj(
            properties = mapOf(
                "score" to Schema.integer(),
                "reason_for_score" to Schema.string(),
                "examples" to Schema.array(
                    items = Schema.string()
                )
            )
        ),
        "grammar_accuracy" to Schema.obj(
            properties = mapOf(
                "score" to Schema.integer(),
                "reason_for_score" to Schema.string(),
                "examples" to Schema.array(
                    items = Schema.string()
                )
            )
        ),
        "pronunciation" to Schema.obj(
            properties = mapOf(
                "score" to Schema.integer(),
                "reason_for_score" to Schema.string(),
                "examples" to Schema.array(
                    items = Schema.string()
                )
            )
        ),
        "speaking_rate" to Schema.obj(
            properties = mapOf(
                "score" to Schema.integer(),
                "reason_for_score" to Schema.string(),
                "examples" to Schema.array(
                    items = Schema.string()
                ),
                "words_per_minute" to Schema.integer(),
            )
        ),
    )
)