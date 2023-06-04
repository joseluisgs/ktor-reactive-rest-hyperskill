#!/usr/bin/env bash

## Server KeyStore: Private Key + Public Certificate (PKCS12)
keytool -genkeypair -alias serverKeyPair -keyalg RSA -keysize 4096 -validity 365 -storetype PKCS12 -keystore server_keystore.p12 -storepass 1234567

## Server KeyStore: Private Key + Public Certificate (JKS)
keytool -genkeypair -alias serverKeyPair -keyalg RSA -keysize 4096 -validity 365 -storetype JKS -keystore server_keystore.jks -storepass 1234567