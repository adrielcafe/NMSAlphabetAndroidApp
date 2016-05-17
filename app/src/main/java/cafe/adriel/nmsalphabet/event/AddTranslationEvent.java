package cafe.adriel.nmsalphabet.event;

import cafe.adriel.nmsalphabet.model.AlienWord;

public class AddTranslationEvent {
    public final AlienWord word;

    public AddTranslationEvent(AlienWord word){
        this.word = word;
    }
}