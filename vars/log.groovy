def call(String message) {
    echo "${message}";
}

def info(String message) {
    echo "INFO: ${message}"
}

def warn(String message) {
    echo "WARNING: ${message}"
}

def error(String message) {
    echo "ERROR: ${message}"
}