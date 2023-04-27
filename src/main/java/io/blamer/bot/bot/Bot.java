/*
 * MIT License
 *
 * Copyright (c) Copyright (c) 2023 Blamer.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.blamer.bot.bot;

import io.blamer.bot.answer.generator.MessageGenerator;
import io.blamer.bot.configuration.BotConfiguration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

@Slf4j
@Service
public class Bot extends TelegramLongPollingBot {

    private final BotConfiguration configuration;

    private final Map<String, MessageGenerator> generators;


    public Bot(
        final BotConfiguration configuration,
        final Map<String, MessageGenerator> generators
    ) throws TelegramApiException {
        this.configuration = configuration;
        this.generators = generators;
        this.execute(
            new SetMyCommands(
                configuration.commands(),
                new BotCommandScopeDefault(),
                null
            )
        );
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(final Update update) {
        if (update.hasMessage()) {
            final MessageGenerator generator = generators.get(update.getMessage().getText());
            if (null == generator) {
                return;
            }
            final SendMessage message = generator.messageFromUpdate(update);
            this.execute(message);
        }
    }

    @Override
    public String getBotUsername() {
        return this.configuration.getName();
    }

    @Override
    public String getBotToken() {
        return this.configuration.getToken();
    }
}