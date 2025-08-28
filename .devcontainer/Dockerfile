FROM ubuntu:24.04 as base

# Install basic packages first (these rarely change)
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    ca-certificates \
    gnupg \
    tar \
    xz-utils \
    git \
    && rm -rf /var/lib/apt/lists/*

# Node.js installation stage
FROM base as nodejs-installer
RUN wget https://nodejs.org/dist/v22.18.0/node-v22.18.0-linux-arm64.tar.xz \
    && tar -xJf node-v22.18.0-linux-arm64.tar.xz -C /opt/ \
    && mv /opt/node-v22.18.0-linux-arm64 /opt/nodejs \
    && rm node-v22.18.0-linux-arm64.tar.xz

# Java installation stage  
FROM base as java-installer
RUN wget -O zulu25.tar.gz https://cdn.azul.com/zulu/bin/zulu25.0.47-beta-jdk25.0.0-beta.34-linux_aarch64.tar.gz \
    && tar -xzf zulu25.tar.gz -C /opt/ \
    && mv /opt/zulu25.0.47-beta-jdk25.0.0-beta.34-linux_aarch64 /opt/jdk25 \
    && rm zulu25.tar.gz

# Final stage - combine everything
FROM base as final

# Copy Node.js from nodejs-installer stage
COPY --from=nodejs-installer /opt/nodejs /opt/nodejs

# Copy Java from java-installer stage
COPY --from=java-installer /opt/jdk25 /opt/jdk25

# Set environment variables
ENV PATH=/opt/nodejs/bin:$PATH
ENV JAVA_HOME=/opt/jdk25
ENV PATH=$JAVA_HOME/bin:$PATH

# Install additional packages (these can change more frequently)
RUN apt-get update && apt-get install -y \
    graphviz \
    libpcap-dev \
    && rm -rf /var/lib/apt/lists/*

# Verify installations
RUN node --version && npm --version && java --version && git --version