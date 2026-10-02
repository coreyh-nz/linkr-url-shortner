package nz.coreyh.linkr

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LinkrApplication

fun main(args: Array<String>) {
    runApplication<LinkrApplication>(*args)
}
