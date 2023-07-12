@Library('unity') _

pipeline {
    environment {
        AUTO_DETECT_UNITY_VERSION = 'true'
//        AUTO_DETECT_UNITY_VERSION = 'false'
//        UNITY_VERSION = '2022.3.1f1'
        BUILD_TARGET = 'StandaloneWindows64'
//        BUILD_TARGET = 'StandaloneLinux64'
        PROJECT_DIR = '.'
        GIT_URL = 'https://bitbucket.org/prosaas/cryptotanks-unity.git'
        GIT_BRANCH = 'feature/unity_2022_1_dev_gs'
        GIT_CREDENTIALS_ID = 'bitbucket'

        EXTRA_SCRIPT_DEFINES = 'UNITY_SERVER'
        EXECUTABLE_NAME = 'gs'
    }

    agent {
        label 'win'
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
                            extraScriptingDefines : env.EXTRA_SCRIPT_DEFINES.split('[, ]'),
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
                    def buildArchivePath = "${BUILD_TAG}.zip"
                    zip zipFile: buildArchivePath, dir: env.OUTPUT_PATH, overwrite: true, archive: true
                }
            }
        }
    }
}
