package com.local.connect.event.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 공공데이터포털 Tour API 응답 파싱 DTO.
 * items 필드가 "" / 단일 객체 / 배열 세 가지 형태로 내려오는 특성에 대응.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TourApiItemDto {

    private Response response;

    @Getter @Setter @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        private Body body;
    }

    @Getter @Setter @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        @JsonDeserialize(using = ItemsDeserializer.class)
        private Items items;
        private int totalCount;
    }

    @Getter @Setter
    public static class Items {
        private List<Item> item = new ArrayList<>();
    }

    @Getter @Setter @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private String contentid;
        private String title;
        private String addr1;
        private String eventstartdate;
        private String eventenddate;
        private String firstimage;
        private String tel;
    }

    public static class ItemsDeserializer extends JsonDeserializer<Items> {
        @Override
        public Items deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            Items items = new Items();

            if (p.currentToken() == JsonToken.VALUE_STRING) {
                return items; // "items": "" 빈 결과
            }

            ObjectMapper mapper = (ObjectMapper) p.getCodec();
            JsonNode node = mapper.readTree(p);
            JsonNode itemNode = node.get("item");

            if (itemNode == null || itemNode.isNull() || itemNode.isMissingNode()) {
                return items;
            }

            if (itemNode.isArray()) {
                for (JsonNode n : itemNode) {
                    items.getItem().add(mapper.treeToValue(n, Item.class));
                }
            } else if (itemNode.isObject()) {
                items.getItem().add(mapper.treeToValue(itemNode, Item.class));
            }

            return items;
        }
    }
}
