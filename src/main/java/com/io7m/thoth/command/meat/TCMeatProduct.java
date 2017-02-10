/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.thoth.command.meat;

import com.io7m.thoth.command.api.ThothListenerType;
import com.io7m.thoth.command.api.ThothResponse;
import javaslang.collection.List;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * A listener that randomly comments on meat.
 */

@Component(immediate = true, service = ThothListenerType.class)
public final class TCMeatProduct implements ThothListenerType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(TCMeatProduct.class);
  }

  private final Random random;
  private List<String> products;

  public TCMeatProduct()
  {
    this.random = new Random();
  }

  @Activate
  public void onActivate()
  {
    this.products = List.empty();

    try (final InputStream stream =
           TCMeatProduct.class.getResourceAsStream("products.txt")) {
      try (final BufferedReader reader =
             new BufferedReader(new InputStreamReader(stream))) {
        while (true) {
          final String line = reader.readLine();
          if (line == null) {
            break;
          }
          this.products = this.products.append(line);
        }
      }

      LOG.debug(
        "loaded {} meat products",
        Integer.valueOf(this.products.size()));
    } catch (final IOException e) {
      LOG.error("i/o error when reading products: ", e);
    }
  }

  @Override
  public String group()
  {
    return "meat";
  }

  @Override
  public String name()
  {
    return "commenter";
  }

  @Override
  public List<ThothResponse> receive(
    final String text)
  {
    if (text.toUpperCase().contains("MEAT")) {
      LOG.debug("meat encountered");
      if (Math.random() <= 0.333333) {
        LOG.debug("meat chance reached");
        return List.of(ThothResponse.of(this.product()));
      }
    }
    return List.empty();
  }

  private String product()
  {
    final int index = this.random.nextInt(this.products.size());
    LOG.debug("index {}", Integer.valueOf(index));
    final String product = this.products.get(index);
    LOG.debug("product {}", product);
    return product;
  }
}
