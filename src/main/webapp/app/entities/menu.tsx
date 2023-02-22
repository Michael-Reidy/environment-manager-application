import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/environment-my-suffix">
        <Translate contentKey="global.menu.entities.environmentMySuffix" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/logical-location-my-suffix">
        <Translate contentKey="global.menu.entities.logicalLocationMySuffix" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/namespace-my-suffix">
        <Translate contentKey="global.menu.entities.namespaceMySuffix" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/setting-my-suffix">
        <Translate contentKey="global.menu.entities.settingMySuffix" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
