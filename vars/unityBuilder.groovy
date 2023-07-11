def build(def args,
          String projectDir = '.',
          String unityHubPath = '',
          Boolean autoDetectUnityVersion = true,
          String unityVersion = null,
          String unityRevision = null,
          String[] scenes = null,
          String buildTarget = null,
          Boolean serverMode = false,
          String additionalParameters = null,
          String[] extraScriptingDefines = null,
          String preBuildMethod = null,
          String postBuildMethod = null,
          String locationPathName = null
) {
    autoDetectUnityVersion = args.autoDetectUnityVersion != null ? args.autoDetectUnityVersion : autoDetectUnityVersion
    unityHubPath = args.unityHubPath ?: unityHubPath
    projectDir = args.projectDir ?: projectDir
    scenes = args.scenes ?: scenes
    buildTarget = args.buildTarget ?: buildTarget
    serverMode = args.serverMode != null ? args.serverMode : serverMode
    additionalParameters = args.additionalParameters ?: additionalParameters ?: ''
    extraScriptingDefines = args.extraScriptingDefines ?: extraScriptingDefines
    preBuildMethod = args.preBuildMethod ?: preBuildMethod
    postBuildMethod = args.postBuildMethod ?: postBuildMethod
    locationPathName = args.locationPathName ?: locationPathName

    if (autoDetectUnityVersion) {
        (unityVersion, unityRevision) = getProjectUnityVersionAndRevision(projectDir)
        log.info("required unityVersion: ${unityVersion} (${unityRevision})")
    } else {
        unityVersion = args.unityVersion ?: unityVersion
        unityRevision = args.unityRevision ?: unityRevision
    }

    unityHub.init(unityHubPath)
    def unityPath = unityHub.getUnityPath(unityVersion, unityRevision, false)
    unity.init(unityPath)

    buildOptions = new HashMap<String, Object>()

    buildOptions['locationPathName'] = locationPathName
    buildOptions['buildTarget'] = buildTarget

    if (buildOptions) {
        buildOptions['scenes'] = scenes
    }
    if (buildOptions) {
        buildOptions['extraScriptingDefines'] = extraScriptingDefines
    }
    if (buildOptions) {
        buildOptions['preBuildMethod'] = preBuildMethod
    }
    if (buildOptions) {
        buildOptions['postBuildMethod'] = postBuildMethod
    }
    buildOptions['enableHeadlessMode'] = serverMode
    if (serverMode) {
        buildOptions['buildSubTarget'] = 'Server'
    }

    dir('Assets/Editor') {
        writeFile file: 'JenkinsBuilder.cs', text: libraryResource('JenkinsBuilder.cs')
    }
    writeJSON file: 'ci_build_options.json', json: buildOptions
    echo 'ci_build_options.json'
    echo writeJSON(json: buildOptions, returnText: true)

    additionalParameters += ' -ciOptionsFile ci_build_options.json'
    unity.execute(projectDir: projectDir, methodToExecute: 'JenkinsBuilder.Build', buildTarget: buildTarget, noGraphics: serverMode, additionalParameters: additionalParameters)
}

def getProjectUnityVersionAndRevision(String projectDir) {
    final def expectedLineStart = 'm_EditorVersionWithRevision: '
    def projectVersionPath = "${projectDir}/ProjectSettings/ProjectVersion.txt"
    if (file.exists(projectVersionPath)) {
        String text = readFile(projectVersionPath)
        for (final def line in text.readLines()) {
            if (line.startsWith(expectedLineStart)) {
                def (unityVersion, unityRevision) = line.substring(expectedLineStart.size()).split(' ')
                return [unityVersion, unityRevision.substring(1, unityRevision.size() - 2)]
            }
        }
    }
    return ['', '']
}