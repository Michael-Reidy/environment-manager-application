import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './namespace-my-suffix.reducer';

export const NamespaceMySuffixDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const namespaceEntity = useAppSelector(state => state.namespace.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="namespaceDetailsHeading">
          <Translate contentKey="environmentManagerApplicationApp.namespace.detail.title">Namespace</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{namespaceEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="environmentManagerApplicationApp.namespace.name">Name</Translate>
            </span>
          </dt>
          <dd>{namespaceEntity.name}</dd>
          <dt>
            <Translate contentKey="environmentManagerApplicationApp.namespace.environment">Environment</Translate>
          </dt>
          <dd>{namespaceEntity.environment ? namespaceEntity.environment.id : ''}</dd>
          <dt>
            <Translate contentKey="environmentManagerApplicationApp.namespace.namespace">Namespace</Translate>
          </dt>
          <dd>{namespaceEntity.namespace ? namespaceEntity.namespace.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/namespace-my-suffix" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/namespace-my-suffix/${namespaceEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default NamespaceMySuffixDetail;
