package commands.base

activeContext = 'main'

welcome = { -> '''\


Welcome to the
 .--.                 .    .-.  .               .      .                 .-. .        . .
:                     |   (   )_|_              |      |                (   )|        | |
|    .-.  .--.--. .-. |    `-.  |  .-.  .--. .-.| .-.  | .-. .--. .-.    `-. |--. .-. | |
:   (   | |  |  |(.-' |   (   ) | (   | |  |(   |(   | |(   )|  |(.-'   (   )|  |(.-' | |
 `--'`-'`-'  '  `-`--'`-   `-'  `-'`-'`-'  `-`-'`-`-'`-`-`-' '  `-`--'   `-' '  `-`--'`-`-
                                                                          Powered by CRaSH


'''
}

prompt = { ->
    "standalone-shell (${activeContext})>"
}