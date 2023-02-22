import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getNamespaces } from 'app/entities/namespace-my-suffix/namespace-my-suffix.reducer';
import { IEnvironmentMySuffix } from 'app/shared/model/environment-my-suffix.model';
import { getEntities as getEnvironments } from 'app/entities/environment-my-suffix/environment-my-suffix.reducer';
import { INamespaceMySuffix } from 'app/shared/model/namespace-my-suffix.model';
import { getEntity, updateEntity, createEntity, reset } from './namespace-my-suffix.reducer';

export const NamespaceMySuffixUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const namespaces = useAppSelector(state => state.namespace.entities);
  const environments = useAppSelector(state => state.environment.entities);
  const namespaceEntity = useAppSelector(state => state.namespace.entity);
  const loading = useAppSelector(state => state.namespace.loading);
  const updating = useAppSelector(state => state.namespace.updating);
  const updateSuccess = useAppSelector(state => state.namespace.updateSuccess);

  const handleClose = () => {
    navigate('/namespace-my-suffix' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getNamespaces({}));
    dispatch(getEnvironments({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...namespaceEntity,
      ...values,
      namespace: namespaces.find(it => it.id.toString() === values.namespace.toString()),
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
          ...namespaceEntity,
          environment: namespaceEntity?.environment?.id,
          namespace: namespaceEntity?.namespace?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="environmentManagerApplicationApp.namespace.home.createOrEditLabel" data-cy="NamespaceCreateUpdateHeading">
            <Translate contentKey="environmentManagerApplicationApp.namespace.home.createOrEditLabel">Create or edit a Namespace</Translate>
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
                  id="namespace-my-suffix-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('environmentManagerApplicationApp.namespace.name')}
                id="namespace-my-suffix-name"
                name="name"
                data-cy="name"
                type="text"
              />
              <ValidatedField
                id="namespace-my-suffix-environment"
                name="environment"
                data-cy="environment"
                label={translate('environmentManagerApplicationApp.namespace.environment')}
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
              <ValidatedField
                id="namespace-my-suffix-namespace"
                name="namespace"
                data-cy="namespace"
                label={translate('environmentManagerApplicationApp.namespace.namespace')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/namespace-my-suffix" replace color="info">
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

export default NamespaceMySuffixUpdate;
