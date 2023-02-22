import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IEnvironmentMySuffix } from 'app/shared/model/environment-my-suffix.model';
import { getEntities as getEnvironments } from 'app/entities/environment-my-suffix/environment-my-suffix.reducer';
import { ILogicalLocationMySuffix } from 'app/shared/model/logical-location-my-suffix.model';
import { getEntity, updateEntity, createEntity, reset } from './logical-location-my-suffix.reducer';

export const LogicalLocationMySuffixUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const environments = useAppSelector(state => state.environment.entities);
  const logicalLocationEntity = useAppSelector(state => state.logicalLocation.entity);
  const loading = useAppSelector(state => state.logicalLocation.loading);
  const updating = useAppSelector(state => state.logicalLocation.updating);
  const updateSuccess = useAppSelector(state => state.logicalLocation.updateSuccess);

  const handleClose = () => {
    navigate('/logical-location-my-suffix' + location.search);
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
    const entity = {
      ...logicalLocationEntity,
      ...values,
      environment: environments.find(it => it.id.toString() === values.environment.toString()),
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
          ...logicalLocationEntity,
          environment: logicalLocationEntity?.environment?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="environmentManagerApplicationApp.logicalLocation.home.createOrEditLabel" data-cy="LogicalLocationCreateUpdateHeading">
            <Translate contentKey="environmentManagerApplicationApp.logicalLocation.home.createOrEditLabel">
              Create or edit a LogicalLocation
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
                  id="logical-location-my-suffix-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('environmentManagerApplicationApp.logicalLocation.name')}
                id="logical-location-my-suffix-name"
                name="name"
                data-cy="name"
                type="text"
              />
              <ValidatedField
                id="logical-location-my-suffix-environment"
                name="environment"
                data-cy="environment"
                label={translate('environmentManagerApplicationApp.logicalLocation.environment')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/logical-location-my-suffix" replace color="info">
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

export default LogicalLocationMySuffixUpdate;
