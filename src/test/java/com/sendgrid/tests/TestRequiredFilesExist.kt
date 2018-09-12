package com.sendgrid.tests

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.on
import java.io.File
import kotlin.test.assertTrue

class TestRequiredFilesExist : Spek({

    given("our config files") {
        // ./Docker or docker/Docker
        on("check Docker exists") {
            val dockerExists = File("./Dockerfile").exists() || File("./docker/Dockerfile").exists()
            assertTrue(dockerExists)
        }

        // // ./docker-compose.yml or ./docker/docker-compose.yml
        // @Test public void checkDockerComposeExists() {
        //     boolean dockerComposeExists = new File("./docker-compose.yml").exists() ||
        //     new File("./docker/docker-compose.yml").exists();
        //     assertTrue(dockerComposeExists);
        // }

        // ./.env_sample
        on("check .env_sample exists") {
            assertTrue(File("./.env_sample").exists())
        }

        // ./.gitignore
        on("check .gitignore exists") {
            assertTrue(File("./.gitignore").exists())
        }

        // ./.travis.yml
        on("check .travis.yml exists") {
            assertTrue(File("./.travis.yml").exists())
        }

        // ./.codeclimate.yml
        on("check .codeclimate.yml exists") {
            assertTrue(File("./.codeclimate.yml").exists())
        }

        // ./CHANGELOG.md
        on("check CHANGELOG exists") {
            assertTrue(File("./CHANGELOG.md").exists())
        }

        // ./CODE_OF_CONDUCT.md
        on("check Code of Conduct exists") {
            assertTrue(File("./CODE_OF_CONDUCT.md").exists())
        }

        // ./CONTRIBUTING.md
        on("check the Contributing Guide exists") {
            assertTrue(File("./CONTRIBUTING.md").exists())
        }

        // ./.github/ISSUE_TEMPLATE
        on("check that the Issues template exists") {
            assertTrue(File("./.github/ISSUE_TEMPLATE").exists())
        }

        // ./LICENSE.md
        on("check that the License exists") {
            assertTrue(File("./LICENSE.md").exists())
        }

        // ./.github/PULL_REQUEST_TEMPLATE
        on("check that the Pull Request template exists") {
            assertTrue(File("./.github/PULL_REQUEST_TEMPLATE").exists())
        }

        // ./README.md
        on("check that the README exists") {
            assertTrue(File("./README.md").exists())
        }

        // ./TROUBLESHOOTING.md
        on("check that the TroubleShooting Guide exists") {
            assertTrue(File("./TROUBLESHOOTING.md").exists())
        }

        // ./USAGE.md
        on(" check that the Usage Guide exists") {
            assertTrue(File("./USAGE.md").exists())
        }

        // ./USE_CASES.md
        on("check UseCases") {
            assertTrue(File("./USE_CASES.md").exists())
        }
    }
})
