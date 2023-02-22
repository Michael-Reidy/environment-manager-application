import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './setting-my-suffix.reducer';

export const SettingMySuffixDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const settingEntity = useAppSelector(state => state.setting.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="settingDetailsHeading">
          <Translate contentKey="environmentManagerApplicationApp.setting.detail.title">Setting</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{settingEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="environmentManagerApplicationApp.setting.name">Name</Translate>
            </span>
          </dt>
          <dd>{settingEntity.name}</dd>
          <dt>
            <span id="valueType">
              <Translate contentKey="environmentManagerApplicationApp.setting.valueType">Value Type</Translate>
            </span>
          </dt>
          <dd>{settingEntity.valueType}</dd>
          <dt>
            <span id="expressionType">
              <Translate contentKey="environmentManagerApplicationApp.setting.expressionType">Expression Type</Translate>
            </span>
          </dt>
          <dd>{settingEntity.expressionType}</dd>
          <dt>
            <span id="value">
              <Translate contentKey="environmentManagerApplicationApp.setting.value">Value</Translate>
            </span>
          </dt>
          <dd>{settingEntity.value}</dd>
          <dt>
            <Translate contentKey="environmentManagerApplicationApp.setting.namespace">Namespace</Translate>
          </dt>
          <dd>{settingEntity.namespace ? settingEntity.namespace.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/setting-my-suffix" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/setting-my-suffix/${settingEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default SettingMySuffixDetail;
