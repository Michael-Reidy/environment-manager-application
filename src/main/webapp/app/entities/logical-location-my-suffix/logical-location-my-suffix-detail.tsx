import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './logical-location-my-suffix.reducer';

export const LogicalLocationMySuffixDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const logicalLocationEntity = useAppSelector(state => state.logicalLocation.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="logicalLocationDetailsHeading">
          <Translate contentKey="environmentManagerApplicationApp.logicalLocation.detail.title">LogicalLocation</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{logicalLocationEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="environmentManagerApplicationApp.logicalLocation.name">Name</Translate>
            </span>
          </dt>
          <dd>{logicalLocationEntity.name}</dd>
          <dt>
            <Translate contentKey="environmentManagerApplicationApp.logicalLocation.environment">Environment</Translate>
          </dt>
          <dd>{logicalLocationEntity.environment ? logicalLocationEntity.environment.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/logical-location-my-suffix" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/logical-location-my-suffix/${logicalLocationEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default LogicalLocationMySuffixDetail;
