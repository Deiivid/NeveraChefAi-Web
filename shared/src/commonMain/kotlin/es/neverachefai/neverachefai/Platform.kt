package es.neverachefai.neverachefai

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform