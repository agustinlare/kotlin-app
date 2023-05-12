package com.clave.dummy.configuration

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AwsS3Configuration(

    @Value("\${aws.s3.accessKeyId}")
    private val accessKeyId: String?,

    @Value("\${aws.s3.secretAccessKey}")
    private val secretAccessKey: String?

) {

    @Bean
    @ConditionalOnProperty(value = ["aws.credentials.provided"], havingValue = "false", matchIfMissing = true)
    fun s3ClientWithoutCredentials(): AmazonS3 {
        logger.info("Creating Amazon S3 Client without credentials - will use default")

        return AmazonS3ClientBuilder
            .standard()
            .withCredentials(DefaultAWSCredentialsProviderChain())
            .withRegion(Regions.US_EAST_2)
            .build()
    }

    @Bean
    @ConditionalOnProperty(value = ["aws.credentials.provided"], havingValue = "true")
    fun s3ClientWithCredentials(): AmazonS3 {
        logger.info("Creating Amazon S3 Client with provided credentials")

        return AmazonS3ClientBuilder
            .standard()
            .withRegion(Regions.US_EAST_2)
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKeyId, secretAccessKey)))
            .build()
    }

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }
}