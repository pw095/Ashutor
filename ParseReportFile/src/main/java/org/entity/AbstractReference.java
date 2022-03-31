package org.entity;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractReference {

    private Set<String> fieldSet;
    private Set<Emitter> emitterSet;
    private Set<String> auditorSet;
    private Set<ReportPeriod> reportPeriodSet;



    public AbstractReference() {

        this.fieldSet = new HashSet<>();
        this.emitterSet = new HashSet<>();
        this.auditorSet = new HashSet<>();
        this.reportPeriodSet = new HashSet<>();

    }



    public AbstractReference(Set<String> fieldSet, Set<Emitter> emitterSet, Set<String> auditorSet, Set<ReportPeriod> reportPeriodSet) {
        setFieldSet(fieldSet);
        setEmitterSet(emitterSet);
        setAuditorSet(auditorSet);
        setReportPeriodSet(reportPeriodSet);
    }
    public AbstractReference(AbstractReference abstractReference) {
        this(abstractReference.getFieldSet(), abstractReference.getEmitterSet(), abstractReference.auditorSet, abstractReference.getReportPeriodSet());
    }



    protected abstract void clearTempTableFieldSet(Object object);
    protected abstract void tempFieldSet(Object object);
    protected Set<String> getFieldSet() {
        return this.fieldSet;
    }

    protected abstract void setFieldSet(Object obj);
    protected void setFieldSet(Set<String> fieldSet) {
        this.fieldSet.addAll(fieldSet);
    }



    protected abstract void clearTempTableEmitterSet(Object object);
    protected abstract void tempEmitterSet(Object object);
    public Set<Emitter> getEmitterSet() {
        return this.emitterSet;
    }

    protected abstract void setEmitterSet(Object obj);
    protected void setEmitterSet(Set<Emitter> emitterSet) {
        if (emitterSet != null && !emitterSet.isEmpty()) {
            this.emitterSet = new HashSet<>();
            for (Emitter emitter : emitterSet) {
                this.emitterSet.add(new Emitter(emitter));
            }
        }
    }



    protected abstract void clearTempTableAuditorSet(Object object);
    protected abstract void tempAuditorSet(Object object);
    public Set<String> getAuditorSet() {
        return this.auditorSet;
    }

    protected abstract void setAuditorSet(Object obj);
    protected void setAuditorSet(Set<String> auditorSet) {
        this.auditorSet.addAll(auditorSet);
    }



    protected abstract void clearTempTableReportPeriodSet(Object object);
    protected abstract void tempReportPeriodSet(Object object);
    public Set<ReportPeriod> getReportPeriodSet() {
        return this.reportPeriodSet;
    }

    protected abstract void setReportPeriodSet(Object obj);
    protected void setReportPeriodSet(Set<ReportPeriod> reportPeriodSet) {
        if (reportPeriodSet != null && !reportPeriodSet.isEmpty()) {
            this.reportPeriodSet = new HashSet<>();
            for (ReportPeriod reportPeriod : reportPeriodSet) {
                this.reportPeriodSet.add(new ReportPeriod(reportPeriod));
            }
        }
    }

}
