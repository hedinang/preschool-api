package org.example.preschool.configuration;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.example.preschool.model.entity.BaseEntity;
import org.example.preschool.service.SequenceGeneratorService;
import org.example.preschool.util.date.DateUtil;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
@AllArgsConstructor
public class BeforeSaveListener extends AbstractMongoEventListener<BaseEntity> {
    private final SequenceGeneratorService sequenceGeneratorService;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<BaseEntity> event) {
        BaseEntity base = event.getSource();

        if (StringUtils.isBlank(base.getCreatedAt())) base.setCreatedAt(DateUtil.formatDateTime(new Date()));

        if (base.getSequence() == null) base.setSequence(sequenceGeneratorService.next(event.getCollectionName()));

        base.setUpdatedAt(DateUtil.formatDateTime(new Date()));
    }
}
