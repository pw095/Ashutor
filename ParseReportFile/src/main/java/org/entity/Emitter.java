package org.entity;

public class Emitter {

    private String emitterName;
    private String emitterFieldName;

    public String getEmitterName() {
        return emitterName;
    }
    public void setEmitterName(String emitterName) {
        this.emitterName = emitterName;
    }

    public String getEmitterFieldName() {
        return emitterFieldName;
    }
    public void setEmitterFieldName(String emitterFieldName) {
        this.emitterFieldName = emitterFieldName;
    }

    public Emitter() {}

    public Emitter(String emitterName) {
        setEmitterName(emitterName);
    }

    public Emitter(String emitterName, String emitterFieldName) {
        setEmitterName(emitterName);
        setEmitterFieldName(emitterFieldName);
    }

    public Emitter(Emitter emitter) {
        this(emitter.getEmitterName(), emitter.getEmitterFieldName());
    }
}
