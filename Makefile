usage:
	@echo "usage:"
	@echo "   build - builds"
	@echo "   clean-build - clean older compile and builds"
	@echo "   run - runs what's already built"
	@echo "   build-run - builds and runs"
	@echo "   verify - runs integration tests on what's already built"
	@echo "   native - builds a native image"

test:
	mvn clean test  -Dorg.slf4j.simpleLogger.defaultLogLevel=warn

assembly:
	mvn clean compile assembly:single

assembly-run:
	java -jar target/kprime-webapp-1.0-SNAPSHOT-jar-with-dependencies.jar

build:
	mvn package appassembler:assemble  -Dorg.slf4j.simpleLogger.defaultLogLevel=warn

clean-build:
	mvn clean package appassembler:assemble  -Dorg.slf4j.simpleLogger.defaultLogLevel=warn

verify:
	mvn verify appassembler:assemble  -Dorg.slf4j.simpleLogger.defaultLogLevel=warn

run:
	sh target/appassembler/bin/app

cli:
	sh target/appassembler/bin/app cli

build-run: build run

native:
	native-image -jar target/kprime-webapp-1.0-SNAPSHOT-jar-with-dependencies.jar \
		-H:ReflectionConfigurationFiles=reflection.json \
		-H:+JNI \
		-H:Name=kprime-webapp \
		--static
#		--allow-incomplete-classpath \
#		--report-unsupported-elements-at-runtime

deploy:
	scp ./target/kprime-webapp-1.0-SNAPSHOT.jar root@$$DOM:/root/kprime/
	scp ./target/appassembler/repo/unibz/cs/semint/kprime/kprime/1.0-SNAPSHOT/kprime-1.0-SNAPSHOT.jar root@$$DOM:/root/kprime/

deploy-full:
	scp -r target/appassembler/ root@$$DOM:/root/kprime/appassembler/

deploy-bitbucket: clean-build
	zip -r ./target/kprime-$(VERSION).zip ./target/appassembler
	curl -s -u npedot -X POST https://api.bitbucket.org/2.0/repositories/npedot/kprime-webapp/downloads -F files=@target/kprime-$(VERSION).zip
#        curl -s -u npedot -X POST https://api.bitbucket.org/2.0/repositories/npedot/kprime-webapp/downloads -F files=@kprime-beta16.zip

check:
	mvn detekt:check

docker-build: clean-build
	sudo docker build -t kprime .

#	sudo docker run -it -v /home/nipe/.kprime/:/kphome/ -p 7000:7000 --rm kprime-beta13
#		--env-file=env-file.properties
docker-run:

	sudo docker run -it \
		-v /home/nipe/.kprime/:/kprime/  \
		-v /home/nipe/Workspaces/semint-kprime-cases/:/home/nipe/Workspaces/semint-kprime-cases/  \
		-e "KPRIME_HOME=/kprime/" \
		-p 7000:7000 \
		--rm kprime

# make docker-deploy -e "kpver=beta16"
docker-deploy: docker-build
	sudo docker tag kprime nipedot/kprime:$(kpver)
	sudo docker push nipedot/kprime:$(kpver)
