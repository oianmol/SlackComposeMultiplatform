tasks {
    register("installGitHook", Copy::class) {
        from(File(rootProject.rootDir, "scripts/git-hooks/pre-commit"))
        into(File(rootProject.rootDir, ".git/hooks"))
        fileMode = 777
        /*
                from(File(rootProject.rootDir, "scripts/git-hooks/pre-push"))
                into(File(rootProject.rootDir, ".git/hooks"))
                fileMode = 777*/
        getByPath(":app:preBuild").dependsOn(":installGitHook")
    }
}
