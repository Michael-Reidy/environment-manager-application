import environment from 'app/entities/environment-my-suffix/environment-my-suffix.reducer';
import logicalLocation from 'app/entities/logical-location-my-suffix/logical-location-my-suffix.reducer';
import namespace from 'app/entities/namespace-my-suffix/namespace-my-suffix.reducer';
import setting from 'app/entities/setting-my-suffix/setting-my-suffix.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  environment,
  logicalLocation,
  namespace,
  setting,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
