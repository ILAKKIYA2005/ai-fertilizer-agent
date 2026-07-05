package com.fertilizer.agent.config;

import org.springframework.context.annotation.Profile;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.distribution.Distribution;
import de.flapdoodle.embed.mongo.distribution.Platform;
import de.flapdoodle.embed.process.runtime.Network;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ImmutableMongodConfig;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;

/**
 * Forces the embedded MongoDB to use a generic Linux distribution that works on the
 * various Amazon Linux, CentOS, Oracle Linux, etc. environments used by Render.
 * This bypasses the Flapdoodle platform resolver which may not recognise those
 * specific OS variants.
 */
@Configuration

public class MongoConfig {
    @Bean(destroyMethod = "close")
    public MongodProcess embeddedMongo() throws Exception {
        // Choose a MongoDB version that matches the version set in application.properties
        Distribution distro = Distribution.of(Version.Main.V5_0, Platform.Linux_X86_64);
        var mongodConfig = ImmutableMongodConfig.builder()
                .version(distro.version())
                .net(new de.flapdoodle.embed.mongo.config.Net(Network.getFreeServerPort(), false))
                .build();
        var starter = MongodStarter.getDefaultInstance();
        MongodExecutable exe = starter.prepare(mongodConfig);
        return exe.start();
    }
}
