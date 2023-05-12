package com.clave.dummy.controller

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/s3tester")
class S3TestController(
    private val s3Client: AmazonS3
) {

    @GetMapping(
        path = ["/buckets"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun listBuckets() : Flux<Map<String, String>> {
        logger.info("Listing all buckets in S3")
        return Flux
            .fromIterable(s3Client.listBuckets())
            .map { mapOf("bucket_name" to it.name, "creation_date" to it.creationDate.toGMTString(), "owner_id" to it.owner.id)}
            .doOnError{ logger.error("Failed to retrieve buckets from s3 with error ${it.message}")}
    }

    @GetMapping(
        path = ["/buckets/{bucket_name}/files"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getBucketFiles(@PathVariable(name = "bucket_name") bucketName: String) : Flux<Map<String,String>> {
        logger.info("Getting files for bucket $bucketName")
        return Flux.just(bucketName)
            .flatMap {
                try {
                    val objectSummaries = s3Client.listObjects(ListObjectsRequest().withBucketName(bucketName)).objectSummaries
                    Flux.fromIterable(objectSummaries)
                }   catch (ex: AmazonS3Exception){
                    Flux.error(ex)
                }
            }
            .map { mapOf("key" to it.key, "size" to "${it.size / 1024} MB", "last_modified" to it.lastModified.toGMTString()) }
            .onErrorMap(AmazonS3Exception::class.java) { ResponseStatusException(resolve(it.statusCode) ?: INTERNAL_SERVER_ERROR, "${it.errorCode} - ${it.errorMessage}")}
            .doOnError{ logger.error("Failed to retrieve buckets from s3 with error ${it.message}")}
    }


    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

}