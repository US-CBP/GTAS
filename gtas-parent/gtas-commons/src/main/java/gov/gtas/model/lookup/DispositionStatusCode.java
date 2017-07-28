package gov.gtas.model.lookup;

public enum DispositionStatusCode {
    NEW("New Case"),
    OPEN("Case is open"),
    CLOSED("Case is closed"),
    REOPEN("Re-opened case"),
    PENDINGCLOSURE("Case is pending closure")
    ;

    private String codeDesc;
    private DispositionStatusCode(final String codeDesc){
        this.codeDesc = codeDesc;
    }
}
