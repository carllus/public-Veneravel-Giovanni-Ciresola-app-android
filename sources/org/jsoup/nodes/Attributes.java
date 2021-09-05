package org.jsoup.nodes;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;

public class Attributes implements Iterable<Attribute>, Cloneable {
    protected static final String dataPrefix = "data-";
    /* access modifiers changed from: private */
    public LinkedHashMap<String, Attribute> attributes = null;

    public String get(String str) {
        Attribute attribute;
        Validate.notEmpty(str);
        LinkedHashMap<String, Attribute> linkedHashMap = this.attributes;
        if (linkedHashMap == null || (attribute = linkedHashMap.get(str.toLowerCase())) == null) {
            return "";
        }
        return attribute.getValue();
    }

    public void put(String str, String str2) {
        put(new Attribute(str, str2));
    }

    public void put(Attribute attribute) {
        Validate.notNull(attribute);
        if (this.attributes == null) {
            this.attributes = new LinkedHashMap<>(2);
        }
        this.attributes.put(attribute.getKey(), attribute);
    }

    public void remove(String str) {
        Validate.notEmpty(str);
        LinkedHashMap<String, Attribute> linkedHashMap = this.attributes;
        if (linkedHashMap != null) {
            linkedHashMap.remove(str.toLowerCase());
        }
    }

    public boolean hasKey(String str) {
        LinkedHashMap<String, Attribute> linkedHashMap = this.attributes;
        return linkedHashMap != null && linkedHashMap.containsKey(str.toLowerCase());
    }

    public int size() {
        LinkedHashMap<String, Attribute> linkedHashMap = this.attributes;
        if (linkedHashMap == null) {
            return 0;
        }
        return linkedHashMap.size();
    }

    public void addAll(Attributes attributes2) {
        if (attributes2.size() != 0) {
            if (this.attributes == null) {
                this.attributes = new LinkedHashMap<>(attributes2.size());
            }
            this.attributes.putAll(attributes2.attributes);
        }
    }

    public Iterator<Attribute> iterator() {
        return asList().iterator();
    }

    public List<Attribute> asList() {
        if (this.attributes == null) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList(this.attributes.size());
        for (Map.Entry<String, Attribute> value : this.attributes.entrySet()) {
            arrayList.add(value.getValue());
        }
        return Collections.unmodifiableList(arrayList);
    }

    public Map<String, String> dataset() {
        return new Dataset();
    }

    public String html() {
        StringBuilder sb = new StringBuilder();
        html(sb, new Document("").outputSettings());
        return sb.toString();
    }

    /* access modifiers changed from: package-private */
    public void html(StringBuilder sb, Document.OutputSettings outputSettings) {
        LinkedHashMap<String, Attribute> linkedHashMap = this.attributes;
        if (linkedHashMap != null) {
            for (Map.Entry<String, Attribute> value : linkedHashMap.entrySet()) {
                sb.append(" ");
                ((Attribute) value.getValue()).html(sb, outputSettings);
            }
        }
    }

    public String toString() {
        return html();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Attributes)) {
            return false;
        }
        LinkedHashMap<String, Attribute> linkedHashMap = this.attributes;
        LinkedHashMap<String, Attribute> linkedHashMap2 = ((Attributes) obj).attributes;
        return linkedHashMap == null ? linkedHashMap2 == null : linkedHashMap.equals(linkedHashMap2);
    }

    public int hashCode() {
        LinkedHashMap<String, Attribute> linkedHashMap = this.attributes;
        if (linkedHashMap != null) {
            return linkedHashMap.hashCode();
        }
        return 0;
    }

    public Attributes clone() {
        if (this.attributes == null) {
            return new Attributes();
        }
        try {
            Attributes attributes2 = (Attributes) super.clone();
            attributes2.attributes = new LinkedHashMap<>(this.attributes.size());
            Iterator<Attribute> it = iterator();
            while (it.hasNext()) {
                Attribute next = it.next();
                attributes2.attributes.put(next.getKey(), next.clone());
            }
            return attributes2;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    private class Dataset extends AbstractMap<String, String> {
        private Dataset() {
            if (Attributes.this.attributes == null) {
                LinkedHashMap unused = Attributes.this.attributes = new LinkedHashMap(2);
            }
        }

        public Set<Map.Entry<String, String>> entrySet() {
            return new EntrySet();
        }

        public String put(String str, String str2) {
            String access$300 = Attributes.dataKey(str);
            String value = Attributes.this.hasKey(access$300) ? ((Attribute) Attributes.this.attributes.get(access$300)).getValue() : null;
            Attributes.this.attributes.put(access$300, new Attribute(access$300, str2));
            return value;
        }

        private class EntrySet extends AbstractSet<Map.Entry<String, String>> {
            private EntrySet() {
            }

            public Iterator<Map.Entry<String, String>> iterator() {
                return new DatasetIterator();
            }

            public int size() {
                int i = 0;
                while (new DatasetIterator().hasNext()) {
                    i++;
                }
                return i;
            }
        }

        private class DatasetIterator implements Iterator<Map.Entry<String, String>> {
            private Attribute attr;
            private Iterator<Attribute> attrIter;

            private DatasetIterator() {
                this.attrIter = Attributes.this.attributes.values().iterator();
            }

            public boolean hasNext() {
                while (this.attrIter.hasNext()) {
                    Attribute next = this.attrIter.next();
                    this.attr = next;
                    if (next.isDataAttribute()) {
                        return true;
                    }
                }
                return false;
            }

            public Map.Entry<String, String> next() {
                return new Attribute(this.attr.getKey().substring(5), this.attr.getValue());
            }

            public void remove() {
                Attributes.this.attributes.remove(this.attr.getKey());
            }
        }
    }

    /* access modifiers changed from: private */
    public static String dataKey(String str) {
        return dataPrefix + str;
    }
}
