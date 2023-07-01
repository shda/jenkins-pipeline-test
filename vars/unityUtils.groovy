def getProjectUnityVersionAndRevision(String projectDir) {
    final def expectedLineStart = 'm_EditorVersionWithRevision: '
    def projectVersionPath = "${projectDir}/ProjectSettings/ProjectVersion.txt"
    if (file.exists(projectVersionPath)) {
        String text = readFile projectVersionPath
        for (final def line in text.readLines()) {
            if (line.startsWith(expectedLineStart)) {
                def (unityVersion, unityRevision) = line.substring(expectedLineStart.size()).split(' ')
                return [unityVersion, unityRevision.substring(1, unityRevision.size() - 2)]
            }
        }
    }
    return ['', '']
}
