@Library('unity') _

pipeline {
    environment {
        AUTO_DETECT_UNITY_VERSION = 'true'
//        AUTO_DETECT_UNITY_VERSION = 'false'
//        UNITY_VERSION = '2022.3.1f1'
        BUILD_TARGET = 'WebGL' //Required
//        BUILD_TARGET = 'StandaloneLinux64'
        PROJECT_DIR = '.' //Required
        GIT_URL = 'https://bitbucket.org/prosaas/cryptotanks-unity.git' //Required
        GIT_BRANCH = 'master' //Required
        GIT_CREDENTIALS_ID = 'bitbucket'

        EXTRA_SCRIPT_DEFINES = ''

        CLEAR_BUILD_DIR_AFTER_COMPLETED = 'true'
        CLEAR_BUILD_ARTIFACT_AFTER_COMPLETED = 'true'
    }

    agent {
        label 'unity && unity-pro'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: env.GIT_BRANCH, credentialsId: env.GIT_CREDENTIALS_ID, url: env.GIT_URL
            }
        }
        stage('Build') {
            steps {
                script {
                    def options = [
                            unityHubPath          : UNITY_HUB_PATH,
                            autoDetectUnityVersion: env.AUTO_DETECT_UNITY_VERSION.toBoolean(),
                            unityVersion          : env.UNITY_VERSION,
                            projectDir            : env.PROJECT_DIR,
                            buildTarget           : env.BUILD_TARGET,
                            extraScriptingDefines : env.EXTRA_SCRIPT_DEFINES?.split('[, ]'),
                            buildName             : BUILD_TAG,
                            webgl            : [
                            ]
                    ]
                    def report = unityBuilder.build(options)
                    env.OUTPUT_PATH = report.outputPath
                }
            }
        }
        stage('Zip') {
            steps {
                script {
                    def buildArchivePath = "${BUILD_TAG}.zip"
                    env.BUILD_ARCHIVE_PATH = buildArchivePath
                    zip zipFile: buildArchivePath, dir: env.OUTPUT_PATH, overwrite: true, archive: true
                }
            }
        }
        stage('Cleanup') {
            steps {
                script {
                    if (env.CLEAR_BUILD_DIR_AFTER_COMPLETED.toBoolean()) {
                        deleteDir env.OUTPUT_PATH
                    }
                    if (env.CLEAR_BUILD_ARTIFACT_AFTER_COMPLETED.toBoolean()) {
                        new File(env.BUILD_ARCHIVE_PATH).delete()
                    }
                }

            }
        }
    }
}
