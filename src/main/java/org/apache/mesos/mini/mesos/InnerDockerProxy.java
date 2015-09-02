package org.apache.mesos.mini.mesos;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import org.apache.mesos.mini.container.AbstractContainer;

import java.security.SecureRandom;

public class InnerDockerProxy extends AbstractContainer {

    public static final String PROXY_IMAGE = "paintedfox/tinyproxy";
    public static final String CONTAINER_NAME = "innerDockerProxy";
    public static final String TAG = "latest";
    private static final int PROXY_PORT = 8888;

    private MesosContainer mesosContainer;

    public InnerDockerProxy(DockerClient dockerClient, MesosContainer mesosContainer) {
        super(dockerClient);
        this.mesosContainer = mesosContainer;
    }

    @Override
    protected void pullImage() {
        pullImage(PROXY_IMAGE, TAG);
    }

    @Override
    protected CreateContainerCmd dockerCommand() {
        return dockerClient
                .createContainerCmd(PROXY_IMAGE + ":" + TAG)
                .withName(generateRegistryContainerName())
                .withExposedPorts(ExposedPort.parse("" + getProxyPort()))
                .withPortBindings(PortBinding.parse("0.0.0.0:" + mesosContainer.getDockerPort() + ":" + getProxyPort()));
    }

    String generateRegistryContainerName() {
        return CONTAINER_NAME + "_" + new SecureRandom().nextInt();
    }

    public int getProxyPort() {
        return PROXY_PORT;
    }
}
