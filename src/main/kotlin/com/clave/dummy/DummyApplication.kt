package com.clave.dummy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.reactive.config.EnableWebFlux

@SpringBootApplication
@EnableWebFlux
class DummyApplication
	fun main(args: Array<String>) {
		runApplication<DummyApplication>(*args)
	}



