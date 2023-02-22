import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getEnvironments } from 'app/entities/environment-my-suffix/environment-my-suffix.reducer';
import { IEnvironmentMySuffix } from 'app/shared/model/environment-my-suffix.model';
import { getEntity, updateEntity, createEntity, reset } from './environment-my-suffix.reducer';

export const EnvironmentMySuffixUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const environments = useAppSelector(state => state.environment.entities);
  const environmentEntity = useAppSelector(state => state.environment.entity);
  const loading = useAppSelector(state => state.environment.loading);
  const updating = useAppSelector(state => state.environment.updating);
  const updateSuccess = useAppSelector(state => state.environment.updateSuccess);

  const handleClose = () => {
    navigate('/environment-my-suffix' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getEnvironments({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.startDate = convertDateTimeToServer(values.startDate);
    values.endDate = convertDateTimeToServer(values.endDate);

    const entity = {
      ...environmentEntity,
      ...values,
      inheritsFrom: environments.find(it => it.id.toString() === values.inheritsFrom.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          startDate: displayDefaultDateTime(),
          endDate: displayDefaultDateTime(),
        }
      : {
          ...environmentEntity,
          startDate: convertDateTimeFromServer(environmentEntity.startDate),
          endDate: convertDateTimeFromServer(environmentEntity.endDate),
          inheritsFrom: environmentEntity?.inheritsFrom?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="environmentManagerApplicationApp.environment.home.createOrEditLabel" data-cy="EnvironmentCreateUpdateHeading">
            <Translate contentKey="environmentManagerApplicationApp.environment.home.createOrEditLabel">
              Create or edit a Environment
            </Translate>
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
                  id="environment-my-suffix-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('environmentManagerApplicationApp.environment.name')}
                id="environment-my-suffix-name"
                name="name"
                data-cy="name"
                type="text"
              />
              <ValidatedField
                label={translate('environmentManagerApplicationApp.environment.startDate')}
                id="environment-my-suffix-startDate"
                name="startDate"
                data-cy="startDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('environmentManagerApplicationApp.environment.endDate')}
                id="environment-my-suffix-endDate"
                name="endDate"
                data-cy="endDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="environment-my-suffix-inheritsFrom"
                name="inheritsFrom"
                data-cy="inheritsFrom"
                label={translate('environmentManagerApplicationApp.environment.inheritsFrom')}
                type="select"
              >
                <option value="" key="0" />
                {environments
                  ? environments.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/environment-my-suffix" replace color="info">
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

export default EnvironmentMySuffixUpdate;
