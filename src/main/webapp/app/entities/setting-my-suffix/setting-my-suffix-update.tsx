import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { INamespaceMySuffix } from 'app/shared/model/namespace-my-suffix.model';
import { getEntities as getNamespaces } from 'app/entities/namespace-my-suffix/namespace-my-suffix.reducer';
import { ISettingMySuffix } from 'app/shared/model/setting-my-suffix.model';
import { ValueType } from 'app/shared/model/enumerations/value-type.model';
import { ExpressionType } from 'app/shared/model/enumerations/expression-type.model';
import { getEntity, updateEntity, createEntity, reset } from './setting-my-suffix.reducer';

export const SettingMySuffixUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const namespaces = useAppSelector(state => state.namespace.entities);
  const settingEntity = useAppSelector(state => state.setting.entity);
  const loading = useAppSelector(state => state.setting.loading);
  const updating = useAppSelector(state => state.setting.updating);
  const updateSuccess = useAppSelector(state => state.setting.updateSuccess);
  const valueTypeValues = Object.keys(ValueType);
  const expressionTypeValues = Object.keys(ExpressionType);

  const handleClose = () => {
    navigate('/setting-my-suffix' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getNamespaces({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...settingEntity,
      ...values,
      namespace: namespaces.find(it => it.id.toString() === values.namespace.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          valueType: 'BOOLEAN',
          expressionType: 'SIMPLE',
          ...settingEntity,
          namespace: settingEntity?.namespace?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="environmentManagerApplicationApp.setting.home.createOrEditLabel" data-cy="SettingCreateUpdateHeading">
            <Translate contentKey="environmentManagerApplicationApp.setting.home.createOrEditLabel">Create or edit a Setting</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="setting-my-suffix-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('environmentManagerApplicationApp.setting.name')}
                id="setting-my-suffix-name"
                name="name"
                data-cy="name"
                type="text"
              />
              <ValidatedField
                label={translate('environmentManagerApplicationApp.setting.valueType')}
                id="setting-my-suffix-valueType"
                name="valueType"
                data-cy="valueType"
                type="select"
              >
                {valueTypeValues.map(valueType => (
                  <option value={valueType} key={valueType}>
                    {translate('environmentManagerApplicationApp.ValueType.' + valueType)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('environmentManagerApplicationApp.setting.expressionType')}
                id="setting-my-suffix-expressionType"
                name="expressionType"
                data-cy="expressionType"
                type="select"
              >
                {expressionTypeValues.map(expressionType => (
                  <option value={expressionType} key={expressionType}>
                    {translate('environmentManagerApplicationApp.ExpressionType.' + expressionType)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('environmentManagerApplicationApp.setting.value')}
                id="setting-my-suffix-value"
                name="value"
                data-cy="value"
                type="text"
              />
              <ValidatedField
                id="setting-my-suffix-namespace"
                name="namespace"
                data-cy="namespace"
                label={translate('environmentManagerApplicationApp.setting.namespace')}
                type="select"
              >
                <option value="" key="0" />
                {namespaces
                  ? namespaces.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/setting-my-suffix" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default SettingMySuffixUpdate;
