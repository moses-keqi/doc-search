package com.moese.file.evernote;

import com.moese.file.utils.DateUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Stack;

/**
 *
 * Title: EverNoteSaxHandler.java
 *
 * @author zxc
 * @time 2018/7/3 下午4:23
 */
public class EverNoteSaxHandler extends DefaultHandler {

    private EverNoteCallBack everNoteCallBack;

    public EverNoteSaxHandler(EverNoteCallBack everNoteCallBack){
        this.everNoteCallBack = everNoteCallBack;
    }
    private Stack<String> tagStack=new Stack<>();
    private EverNote everNote;
    private boolean noteAttribute;

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if(qName.equals("note")){
            everNote=new EverNote();
        }
        if(qName.equals("note-attributes")){
            noteAttribute = true;
        }
        tagStack.push(qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        String topElement = tagStack.peek();
        String data = new String(ch,start,length);
        if(topElement!=null){
            if(topElement.equals("title")){
                everNote.setTitle(data);
            }
            if(topElement.equals("content")){
                everNote.setContent(data);
            }
            if(topElement.equals("created")){
                everNote.setCreated(DateUtils.parseTZDateString(data));
            }
            if(topElement.equals("updated")){
                everNote.setUpdated(DateUtils.parseTZDateString(data));
            }
            if(noteAttribute){
                if(topElement.equals("author")){
                    everNote.setAuthor(data);
                }
                if(topElement.equals("source")){
                    everNote.setSource(data);
                }
                if(topElement.equals("source-url")){
                    everNote.setSourceUrl(data);
                }
                if(topElement.equals("source-application")){
                    everNote.setSourceApplication(data);
                }
            }
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        tagStack.pop();
        if(qName.equals("note-attributes")){
            noteAttribute=false;
        }
        if(qName.equals("note")){
            everNoteCallBack.onNote(everNote);
        }
    }
}
