# kotlin

S3 Tester

**List all buckets in S3**

GET <APP_URL>/s3tester/buckets

List all files within a bucket in S3
GET <APP_URL>/s3tester/buckets/{bucket_name}/files


S3 Credentials settings
Set the property aws.credentials.provided to true to use local credentials. For example >>

aws.s3.accessKeyId=******************
aws.s3.secretAccessKey=******************