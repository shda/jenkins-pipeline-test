@Library('unity') _

pipeline {
    environment {
        AUTO_DETECT_UNITY_VERSION = 'true'
//        AUTO_DETECT_UNITY_VERSION = 'false'
//        UNITY_VERSION = '2022.3.1f1'
        BUILD_TARGET = 'StandaloneWindows64' //Required
//        BUILD_TARGET = 'StandaloneLinux64'
        PROJECT_DIR = '.' //Required
        GIT_URL = 'https://bitbucket.org/prosaas/cryptotanks-unity.git' //Required
        GIT_BRANCH = 'feature/unity_2022_1_dev_gs' //Required
        GIT_CREDENTIALS_ID = 'bitbucket'

        EXTRA_SCRIPT_DEFINES = 'UNITY_SERVER'
        EXECUTABLE_NAME = 'gs'

        CLEAR_BUILD_DIR_AFTER_COMPLETED = 'true'
    }

    agent {
        label 'unity'
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
                            standalone            : [
                                    serverMode    : true,
                                    executableName: env.EXECUTABLE_NAME
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
                    def buildArchivePath = "${env.OUTPUT_PATH}.zip"
                    zip zipFile: buildArchivePath, dir: env.OUTPUT_PATH, overwrite: true, archive: true
                    new File(buildArchivePath).delete()
                }
            }
        }
        stage('Cleanup') {
            when {
                expression { env.CLEAR_BUILD_DIR_AFTER_COMPLETED.toBoolean() }
            }
            steps {
                dir(env.OUTPUT_PATH) {
                    deleteDir()
                }
            }
        }
    }
}
