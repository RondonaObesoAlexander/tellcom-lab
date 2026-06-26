FROM debian:bullseye AS builder

ARG ASTERISK_VERSION=18.26.0

RUN apt-get update && apt-get install -y \
    wget build-essential cmake \
    libssl-dev libxml2-dev libjansson-dev \
    libedit-dev libsqlite3-dev \
    libsrtp2-dev libpcre3-dev \
    uuid-dev libcurl4-openssl-dev \
    libmariadb-dev libmariadb-dev-compat \
    unixodbc-dev \
    && rm -rf /var/lib/apt/lists/*

RUN wget http://downloads.asterisk.org/pub/telephony/asterisk/releases/asterisk-${ASTERISK_VERSION}.tar.gz \
    && tar -xzf asterisk-${ASTERISK_VERSION}.tar.gz \
    && cd asterisk-${ASTERISK_VERSION} \
    && ./configure --with-pjproject-bundled --with-mysql=yes

RUN cd asterisk-${ASTERISK_VERSION} \
    && make menuselect.makeopts \
    && menuselect/menuselect --enable chan_pjsip \
    && menuselect/menuselect --enable cdr_mysql \
    && make -j$(nproc) \
    && make install \
    && make samples

FROM debian:bullseye

RUN apt-get update && apt-get install -y \
    libssl1.1 libxml2 libjansson4 \
    libedit2 libsqlite3-0 \
    libsrtp2-1 libcurl4 \
    libmariadb3 unixodbc \
    python3 python3-pip python3-flask \
    && rm -rf /var/lib/apt/lists/*

# Copia exhaustiva de binarios y dependencias del sistema
COPY --from=builder /usr/sbin/asterisk /usr/sbin/
COPY --from=builder /usr/lib/asterisk /usr/lib/asterisk
COPY --from=builder /usr/lib/libasterisk* /usr/lib/
COPY --from=builder /etc/asterisk /etc/asterisk
COPY --from=builder /var/lib/asterisk /var/lib/asterisk
COPY --from=builder /var/spool/asterisk /var/spool/asterisk

ENV LD_LIBRARY_PATH=/usr/lib/asterisk:$LD_LIBRARY_PATH
RUN ldconfig

EXPOSE 5060/udp 5060/tcp 10000-10100/udp 5000/tcp

COPY api_provision.py /usr/src/api_provision.py

# Entrypoint robusto de nivel corporativo (Limpia sockets y asegura permisos IPC)
RUN printf '#!/bin/bash\n\
rm -rf /var/run/asterisk/* /var/run/asterisk.ctl\n\
mkdir -p /var/log/asterisk /var/run/asterisk /var/spool/asterisk /etc/asterisk\n\
chmod -R 777 /var/run/asterisk /var/log/asterisk /etc/asterisk\n\
if [ ! -f /etc/asterisk/pjsip.conf ]; then\n\
  echo -e "[transport-udp]\\ntype=transport\\nprotocol=udp\\nbind=0.0.0.0" > /etc/asterisk/pjsip.conf\n\
fi\n\
python3 /usr/src/api_provision.py &\n\
exec /usr/sbin/asterisk -f -p -U root -G root -vvv\n' > /entrypoint.sh \
    && chmod +x /entrypoint.sh

CMD ["/entrypoint.sh"]
