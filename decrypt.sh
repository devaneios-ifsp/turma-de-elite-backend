#!/bin/sh

# --lote para evitar o comando interativo
# --sim para supor "sim" para as perguntas
gpg --quiet --batch --yes --decrypt --passphrase="$FIREBASE_ENCRYPT_KEY" \
--output firebase/firebase-sdk-key.json firebase/firebase-sdk-key.json.gpg
gpg --quiet --batch --yes --decrypt --passphrase="$FIREBASE_ENCRYPT_KEY" \
--output src/main/resources/classroom-credentials.json src/main/resources/classroom-credentials.json.gpg