package com.rene.joao.tpii;

public class NoArvore {
    private int noID;
    private int paiID;
    private boolean leaf;
    private String name;
    private String content;

    public NoArvore(int noID, int paiID, boolean leaf, String name, String content) {
        this.noID = noID;
        this.paiID = paiID;
        this.leaf = leaf;
        this.name = name;
        this.content = content;
    }

    public int getNoID() {
        return noID;
    }

    public void setNoID(int noID) {
        this.noID = noID;
    }

    public int getPaiID() {
        return paiID;
    }

    public void setPaiID(int paiID) {
        this.paiID = paiID;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
