package cafe.adriel.nmsalphabet.event;

import java.util.List;

import cafe.adriel.nmsalphabet.model.AlienWord;
import cafe.adriel.nmsalphabet.model.AlienWordTranslation;

public class TranslationUpdatedEvent {
    public final AlienWord word;
    public final List<AlienWordTranslation> translations;

    public TranslationUpdatedEvent(AlienWord word, List<AlienWordTranslation> translations){
        this.word = word;
        this.translations = translations;
    }
}