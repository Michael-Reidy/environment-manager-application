import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './environment-my-suffix.reducer';

export const EnvironmentMySuffixDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const environmentEntity = useAppSelector(state => state.environment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="environmentDetailsHeading">
          <Translate contentKey="environmentManagerApplicationApp.environment.detail.title">Environment</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{environmentEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="environmentManagerApplicationApp.environment.name">Name</Translate>
            </span>
          </dt>
          <dd>{environmentEntity.name}</dd>
          <dt>
            <span id="startDate">
              <Translate contentKey="environmentManagerApplicationApp.environment.startDate">Start Date</Translate>
            </span>
          </dt>
          <dd>
            {environmentEntity.startDate ? <TextFormat value={environmentEntity.startDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="endDate">
              <Translate contentKey="environmentManagerApplicationApp.environment.endDate">End Date</Translate>
            </span>
          </dt>
          <dd>
            {environmentEntity.endDate ? <TextFormat value={environmentEntity.endDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="environmentManagerApplicationApp.environment.inheritsFrom">Inherits From</Translate>
          </dt>
          <dd>{environmentEntity.inheritsFrom ? environmentEntity.inheritsFrom.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/environment-my-suffix" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/environment-my-suffix/${environmentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default EnvironmentMySuffixDetail;
