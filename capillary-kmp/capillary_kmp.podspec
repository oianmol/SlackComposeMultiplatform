Pod::Spec.new do |spec|
    spec.name                     = 'capillary_kmp'
    spec.version                  = '1.0.0'
    spec.homepage                 = 'https://github.com/oianmol'
    spec.source                   = { :http=> ''}
    spec.authors                  = ''
    spec.license                  = ''
    spec.summary                  = 'SlackCapillary'
    spec.vendored_frameworks      = 'build/cocoapods/framework/capillaryslack.framework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target = '14.1'
    spec.dependency 'capillaryslack'
                
    if !Dir.exist?('build/cocoapods/framework/capillaryslack.framework') || Dir.empty?('build/cocoapods/framework/capillaryslack.framework')
        raise "

        Kotlin framework 'capillaryslack' doesn't exist yet, so a proper Xcode project can't be generated.
        'pod install' should be executed after running ':generateDummyFramework' Gradle task:

            ./gradlew :capillary-kmp:generateDummyFramework

        Alternatively, proper pod installation is performed during Gradle sync in the IDE (if Podfile location is set)"
    end
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':capillary-kmp',
        'PRODUCT_MODULE_NAME' => 'capillaryslack',
    }
                
    spec.script_phases = [
        {
            :name => 'Build capillary_kmp',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                if [ "YES" = "$OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" ]; then
                  echo "Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED environment variable set to \"YES\""
                  exit 0
                fi
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$REPO_ROOT/../gradlew" -p "$REPO_ROOT" $KOTLIN_PROJECT_PATH:syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
            SCRIPT
        }
    ]
                
end