# IAssistant

> EN:

This is my first application using voice commands. It uses google voice APIs to transform voice commands
of the user in interpretable texts in the application. Obviously the set of commands is not very large, but it is scalable.

## How it works:
- Once opened, you must grant the necessary permissions.
- On the initial screen you can see some buttons (some of them available only during playback) and a message.
- The message is for the user to perform the touch to be able to give the voice commands.
- The buttons are:
  - play button: Starts a playlist with all the user's songs and plays it.
  - Pause button: Pauses the playlist started (visible only during playback).
  - Settings button: Opens the screen to set the backlight times and instructs you to go offline.
  - Forward button: Skip to the next song.
  - Rewind button: Returns to the previous song.
  - Sort Button: Switches the order of songs between Normal and Shuffle (visible only during playback).
  - Repeat button: Repeat the last phrase spoken by the assistant.
  - Background button: Exchanges the animation of the background, there are 5 animations available.
- The user can:
  - Using the buttons, start all songs, pause, skip forward or rewind. In addition to change the order of the songs
of the playlist, change the animation, repeat the phrase and go to the settings.
  - Through the voice commands the user can:
    - Ask for permissions if you have not already.
    - Ask the time.
    - Start all songs.
    - Start a specific song.
    - Start songs from a specific folder.
    - Start songs by name.
    - Start songs by the artist.
    - Forward songs.
    - Rewind songs.
    - Pause music.
    - Resume song playback.
    - Stop music.
    - Start a new playlist.
    - Add new songs to a playlist.
    - Move to another background animation.
    - Return to the previous background animation.
- In the settings you can determine how long the application backlight stays on (with and without music)
thus helping to save battery power.
- If you want, you can exit the application (without closing / killing it) without stopping the music.
- To interrupt a voice command, simply tap again (during the "Fale Agora !" message).

## Used components:
- Service
- Media player
- Text to speech
- Speech recognizer
- GifTextView (Droidsonroids)

> PT:

Este é meu primeiro aplicativo usando comandos de voz. Ele utiliza das APIs do google de voz para transformar os comandos de voz
do usuário em textos interpretáveis na aplicação. Obviamente o set de comandos não é muito grande, porém é escalável.

## Como funciona:
- Uma vez aberto, é preciso conceder as permissões necessárias.
- Prosseguindo, na tela inicial é possivel ver alguns botões (alguns disponíveis somente durante a reprodução) e uma instrução.
- A instrução é para que o usuário realize o toque para poder dar os comandos de voz.
- Os botões são:
  - Botão play: Inicia uma playlist com todas a músicas do usuário e a reproduz.
  - Botão pause: Pausa a playlist iniciada (vísivel somente durante a reprodução).
  - Botão avançar: Avança para a próxima música.
  - Botão retroceder: Volta para a música anterior.
  - Botão de configurações: Abre a tela para configurar os tempos da luz de fundo e instrui para o funcionamento offline.
  - Botão de ordenação: Alterna a ordem das músicas entre Normal e Aleatória (vísivel somente durante a reprodução).
  - Botão de repetição: Repete a ultima frase dita pela assistente.
  - Botão de plano de fundo: Troca a animação do fundo, existem 5 animações disponíves.
- O usuário pode:
  - Por meio dos botões, iniciar todas as músicas, pausar, avançar ou retroceder. Além alterar a ordenação da playlist,
trocar a animação, repetir a frase e ir para as configurações.
  - Por meio dos comandos de voz o usuário pode:
    - Pedir as permissões, caso ainda não tenha concedido.
    - Perguntar as horas.
    - Iniciar todas as músicas.
    - Iniciar uma música especifica.
    - Iniciar músicas de uma pasta específica.
    - Iniciar músicas pelo nome.
    - Iniciar músicas pelo artista.
    - Avançar músicas.
    - Retroceder músicas.
    - Pausar músicas.
    - Retomar a reprodução das músicas.
    - Parar músicas.
    - Iniciar uma nova playlist.
    - Adicionar novas músicas em uma playlist em execução.
    - Avançar para outra animação de fundo.
    - Voltar para a animação de fundo anterior.
- Nas configurações é possível determinar o tempo em que a luz de fundo do aplicativo fica ligada (com e sem música) 
ajudando assim na economia de bateria.
- Caso deseje, é possível sair da aplicação (sem encerra-la/mata-la) sem que a música seja interrompida.
- Para interromper um comando de voz, basta tocar novamente (durante a mensagem "Fale Agora !").

## Componentes utilizados:
- Service
- Media player
- Conversão de texto em voz
- Reconhecimento de fala
- GifTextView (Droidsonroids)

## Mapa de comandos (somente disponível em Português [PT-BR])

Cada item em **_negrito e italico_** representa a mensagem apresentada na tela (e provavelmente dita pela assistente),
enquanto que os subitens representam as mensagens que podem ser ditas pelo usuário à assistente (comandos). A seta
a frente dos subitens (->) representa a resposta da assistente ao comando, ou apenas a próxima mensagem.

- **_"Toque para interagir"_**
  - Que horas são -> **_"Agora são HH:MM"_**
  - trocar animação
  - voltar animação
  - Ouvir música -> **_"O que deseja ouvir?"_**

- **_"O que deseja ouvir?"_**
  - Todas/Tudo -> **_"Ok, inicializando o player"_**
  - < Nome da música > -> **_"Ok, inicializando o player"_**
  - < Nome do artista > -> **_"Ok, inicializando o player"_**
  - Pasta < Nome da pasta > -> **_"Ok, inicializando o player"_**
  
- **_"Ok, inicializando o player"_** -> **_"<Nome da música>"_**

- **_"<Nome da música>"_** _(reproduzindo)_
  - Que horas são -> **_"Agora são HH:MM"_**
  - Mudar/próxima música
  - Voltar música/música anterior
  - Ordem aleatória/aleatóriamente
  - Ordem normal
  - Pausar música -> **_"O player está pausado"_**
  - Parar música -> **_"O player foi parado com sucesso"_**
  - Adicionar nova(s) música(s)/Adicionar outra(s) música(s) -> **_"O que deseja adicionar à essa playlist?"_**
  - Ouvir outras músicas -> **_"O que deseja ouvir"_**
  - trocar animação
  - voltar animação
  
**_"O player está pausado"_**
  - Continuar música/reprodução
  - Que horas são -> **_"Agora são HH:MM"_**

**_"O player foi parado com sucesso"_** -> **_"Toque para interagir"_**

**_"O que deseja adicionar à essa playlist?"_**
  - Todas/Tudo -> **_"Novas músicas adicionadas"_**
  - < Nome da música > -> **_"Novas músicas adicionadas"_**
  - < Nome do artista > -> **_"Novas músicas adicionadas"_**
  - Pasta < Nome da pasta > -> **_"Novas músicas adicionadas"_**

**_"Novas músicas adicionadas"_**  -> **_"< Nome da música >"_**

O conjunto de comandos não está totalmente completo, podem haver variações do comando aceitas e outros
comandos não especificados aqui. Para obter um mapa completo, verifique o código fonte.

### FGApps
  
